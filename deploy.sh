#!/bin/bash

SOLUTION_NAME=$1
DOMAIN_NAME=$2
TIMESTAMP=$(date +%Y%m%d%H%M%S)

wait_stack_creation_completed() {
  STACK_NAME=$1
  echo "Waiting for $STACK_NAME to be completed"
  STATUS=$(aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME")
  if [[ ${STATUS} -ne 0 ]]; then

    echo "Creation has failed $STATUS"
    exit ${STATUS}
  fi
}

wait_stack_update_completed() {
  STACK_NAME=$1
  echo "Waiting for $STACK_NAME to be completed"
  STATUS=$(aws cloudformation wait stack-update-complete --stack-name "$STACK_NAME")
  if [[ ${STATUS} -ne 0 ]]; then

    echo "Creation has failed $STATUS"
    exit ${STATUS}
  fi
}

build_app() {
  mvn clean verify
}

upload_artifacts_to_s3() {
  S3_BUCKET=$1
  aws s3 cp --recursive cfn s3://${S3_BUCKET}/${TIMESTAMP}/cfn
  ls -1 | grep 'lambda$' | while IFS= read -r filename; do
    echo "Processing file: '$filename'"

    aws s3 cp ${filename}/target/${filename}.jar s3://${S3_BUCKET}/${TIMESTAMP}/
  done
}

create_s3() {
  CURRENT_SOLUTION_NAME="$SOLUTION_NAME-s3"
  STACK_EXISTS=$(aws cloudformation describe-stacks --stack-name "$CURRENT_SOLUTION_NAME" || echo "create")
  if [ "$STACK_EXISTS" == "create" ]; then
    aws cloudformation create-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/s3-storage.cfn.yaml --parameters ParameterKey=SolutionName,ParameterValue=$CURRENT_SOLUTION_NAME
    wait_stack_creation_completed "$CURRENT_SOLUTION_NAME"
  else
    WAIT_FOR_UPDATE=$(aws cloudformation update-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/s3-storage.cfn.yaml --parameters ParameterKey=SolutionName,UsePreviousValue=true || echo "no updates")
    if [ "$WAIT_FOR_UPDATE" != "no updates" ]; then
      wait_stack_update_completed "$CURRENT_SOLUTION_NAME"
    fi
  fi
}

create_domain_with_certificate() {
  CURRENT_SOLUTION_NAME="$SOLUTION_NAME-domain"
  STACK_EXISTS=$(aws cloudformation describe-stacks --stack-name "$CURRENT_SOLUTION_NAME" || echo "create")
  if [ "$STACK_EXISTS" == "create" ]; then
    aws cloudformation create-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/domain.cfn.yaml --parameters ParameterKey=SolutionName,ParameterValue=$CURRENT_SOLUTION_NAME ParameterKey=DomainName,ParameterValue=$DOMAIN_NAME
    wait_stack_creation_completed "$CURRENT_SOLUTION_NAME"
  else
    WAIT_FOR_UPDATE=$(aws cloudformation update-stack --stack-name "$CURRENT_SOLUTION_NAME" --template-body file://cfn/domain.cfn.yaml --parameters ParameterKey=SolutionName,UsePreviousValue=true ParameterKey=DomainName,UsePreviousValue=true || echo "no updates")
    if [ "$WAIT_FOR_UPDATE" != "no updates" ]; then
      wait_stack_update_completed "$CURRENT_SOLUTION_NAME"
    fi
  fi
}

collect_lambda_names() {
  RESULTS=()
  for DIR in *lambda/; do
      if [ -d "$DIR" ]; then
          DIR_NAME_WITHOUT_SLASH="${DIR%/}"
          PARAMETER_KEY=$(echo "$DIR_NAME_WITHOUT_SLASH" | sed -e 's/-\(.\)/\U\1/g' -e 's/^\(.\)/\U\1/')
          RESULTS+=("ParameterKey=$PARAMETER_KEY,ParameterValue=$DIR_NAME_WITHOUT_SLASH ")
      fi
  done
  if [ ${#RESULTS[@]} -eq 0 ]; then
      >&2 echo "No directories ending with 'lambda' found."
      exit 1
  fi
  echo "${RESULTS[*]}"
}

deploy_solution() {
  S3_BUCKET=$1

  CURRENT_SOLUTION_NAME="$SOLUTION_NAME-paswordless"

  STACK_EXISTS=$(aws cloudformation describe-stacks --stack-name "$CURRENT_SOLUTION_NAME" || echo "create")
  LAMBDAS=$(collect_lambda_names)
  if [ "$STACK_EXISTS" == "create" ]; then
    echo "creating stack..."
    aws cloudformation create-stack --stack-name "$CURRENT_SOLUTION_NAME" \
        --template-url https://${S3_BUCKET}.s3.amazonaws.com/${TIMESTAMP}/cfn/paswordless-login-service.cfn.yaml \
        --parameters ParameterKey=SolutionName,ParameterValue=$CURRENT_SOLUTION_NAME \
                     ParameterKey=S3Bucket,ParameterValue=$S3_BUCKET \
                     ParameterKey=DomainName,ParameterValue=$DOMAIN_NAME \
                     ParameterKey=DomainStack,ParameterValue=$SOLUTION_NAME-domain \
                     ParameterKey=Version,ParameterValue=$TIMESTAMP \
                     ${LAMBDAS} \
        --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
    wait_stack_creation_completed "$CURRENT_SOLUTION_NAME"
  else
    echo "updating stack..."
    WAIT_FOR_UPDATE=$(aws cloudformation update-stack --stack-name "$CURRENT_SOLUTION_NAME" \
                          --template-url https://${S3_BUCKET}.s3.amazonaws.com/${TIMESTAMP}/cfn/paswordless-login-service.cfn.yaml \
                          --parameters ParameterKey=SolutionName,UsePreviousValue=true \
                                       ParameterKey=S3Bucket,UsePreviousValue=true \
                                       ParameterKey=DomainName,UsePreviousValue=true \
                                       ParameterKey=DomainStack,UsePreviousValue=true \
                                       ParameterKey=Version,ParameterValue=$TIMESTAMP \
                                       ${LAMBDAS} \
                          --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM || echo "no updates")
    if [ "$WAIT_FOR_UPDATE" != "no updates" ]; then
      wait_stack_update_completed "$CURRENT_SOLUTION_NAME"
    fi
  fi
}

create_s3
create_domain_with_certificate

S3_BUCKET=$(aws cloudformation describe-stacks --stack-name "$SOLUTION_NAME-s3" --output text  --query 'Stacks[0].Outputs[?OutputKey==`S3Bucket`].OutputValue')
build_app
upload_artifacts_to_s3 "$S3_BUCKET"
deploy_solution "$S3_BUCKET"

SITE_URL=$(aws cloudformation describe-stacks --stack-name "$SOLUTION_NAME-paswordless" --output text  --query 'Stacks[0].Outputs[?OutputKey==`ApiGwFacade`].OutputValue')
echo "============================================"
echo "Visit: $SITE_URL"
echo "============================================"

