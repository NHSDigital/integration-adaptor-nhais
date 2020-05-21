resource "aws_cloudwatch_log_group" "ecs_service_cw_log_group" {
  name = "${local.resource_prefix}-cw_log_group"

  retention_in_days = var.retention_in_days
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-cw_log_group"
  })
}