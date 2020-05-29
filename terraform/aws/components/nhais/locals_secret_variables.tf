locals {
    secret_variables = [
    {
      name = "NHAIS_AMQP_USERNAME"
      valueFrom = data.aws_secretsmanager_secret.mq_username.arn
    },
    {
      name = "NHAIS_AMQP_PASSWORD"
      valueFrom = data.aws_secretsmanager_secret.mq_password.arn
    }
  ]
}

    # {
    #   name = "NHAIS_MONGO_USERNAME"
    #   value = "user"
    # },
    # {
    #   name = "NHAIS_MONGO_PASSWORD"
    #   value = "pass"
    # }