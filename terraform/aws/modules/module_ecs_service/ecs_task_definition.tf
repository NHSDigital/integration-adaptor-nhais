resource "aws_ecs_task_definition" "ecs_task_definition" { 
  name = "${local.resource_prefix}-task_definition"
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-task_definition"
  })
}