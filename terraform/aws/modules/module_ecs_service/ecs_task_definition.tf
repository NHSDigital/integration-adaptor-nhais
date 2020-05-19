resource "aws_ecs_task_definition" "ecs_task_definition" { 
  family = "${local.resource_prefix}-task_definition"
  container_definitions = ""
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-task_definition"
  })
}