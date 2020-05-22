locals {
  default_tags = {
    Project = var.project
    Environment = var.environment
    Component = var.component
  }

    resource_prefix = "${var.project}-${var.environment}-${var.component}"

    environment_variables = [
      {
        name  = "NHAIS_OUTBOUND_SERVER_PORT"
        value = var.nhais_service_container_port
      },
      {
        name  = "NHAIS_LOG_LEVEL"
        value = "DEBUG"
      },
      {
        name = "NHAIS_OUTBOUND_MAX_RETRIES"
        value = 100
      },
      {
        name = "NHAIS_OUTBOUND_RETRY_DELAY"
        value = 3
      },
      {
        name = "NHAIS_OUTBOUND_QUEUE_BROKERS"
        value = data.aws_mq_broker.nhais_mq_broker.instances[0].endpoints[1] # https://www.terraform.io/docs/providers/aws/r/mq_broker.html#attributes-reference
      },
      {
        name = "NHAIS_OUTBOUND_QUEUE_NAME"
        value = "nhais_outbound"
      },
      {
        name = "NHAIS_DYNAMODB_ENDPOINT_URL"
        value = aws_dynamodb_table.nhais_dynamodb.name
      },
    ]
}