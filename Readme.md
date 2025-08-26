# AWS API Gateway with Lambda (Java 21 + Dagger)

This project sets up an AWS API Gateway integrated with an AWS Lambda functions. The Lambda functions are built using Java 21 and leverages the Dagger framework for Dependency Injection.

## Project Structure

Lambda Function: Built with Maven (mvn), using Java 21 and Dagger for DI.

- **API Gateway**: Configured to trigger the Lambda function.
- **Infrastructure**: Built using AWS CloudFormation. The CloudFormation templates are located in the `cfn` folder.
- **Deployment Scripts:**
  - `./deploy <solution_name>`: Deploys the API Gateway and Lambda function.
  - `./undeploy <solution_name>`: Removes the deployed resources.

## Prerequisites

Ensure you have the following installed and configured:

- AWS CLI configured with appropriate credentials
- Java 21
- Maven
- Bash (for running deployment scripts)

## Building the Lambda Functions

Run the following command to build the project:

`mvn clean package`

This will generate a deployment-ready JAR file in the target directory.

## Deployment

To deploy the solution, run:

`./deploy <solution_name>`

This script will:
- Package and upload the Lambda function.
- Create an API Gateway endpoint.
- Configure the necessary permissions.

## Undeployment

To remove the deployed resources, run:

`./undeploy <solution_name>`

This script will:

- Delete the API Gateway configuration.
- Remove the Lambda function.
- Clean up any associated resources.

## Testing the API

After deployment, you can test the API using curl:

`curl -X GET https://<api_gateway_url>/<resource>`

Replace <api_gateway_url> with the actual endpoint provided after deployment.

# The auth flow

The idea is pretty simple, you don't want to handle entire flow of managing you user eg:
- Register account,
- Forgot password, 
- Change password,
- Managing password policy,
- Password rotation etc.

All of this stuff is currently properly handled by the email providers (like google, amazon, etc.), what you want to have instead 
is unique* user, that can perform actions and even in case of security breach etc. your leak will be reduced to data like e-mail / name. 

'*' - unfortunately aliases in this approach will be treated as separated accounts, so there are chances to have one user that is using more than one identity in the system, but if someone wants to cheat the system it'll do it that or another way. 

## 1. Registration

It's not required you can create an account on first logging attempt, this approach is the default one (can be change to collect some data about your customer like name, gender, or even a picture - but from the auth flow perspective it's not required)

So the flow is a follows:
### `POST /auth/generate`
Firstly you have to generate login request this operation is not protected by any auth, so you have to set up throttling on it.
The only one parameter that is required is `email`. Sample payload will look like this:

#### `curl https://<api url>/login/generate -d '{"email": "<provided email>"}'`

This request will create a user entry (if the entry wasn't created yet), and it's going to email to the destination email with login token.

## Notes

The Dagger framework is used for Dependency Injection within the Lambda function to manage components efficiently.
The deployment and undeployment scripts assume proper AWS permissions are in place.


