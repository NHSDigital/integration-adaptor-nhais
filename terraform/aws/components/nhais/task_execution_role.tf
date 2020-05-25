resource "aws_iam_role" "ecs_service_task_execution_role" {
  name = "${local.resource_prefix}-task_execution_role"
  assume_role_policy = data.aws_iam_policy_document.ecs_service_task_execution_policies.json
}

resource "aws_iam_role_policy_attachment" "ecs_service_task_execution_role_attachment {
  role = aws_iam_role.ecs_service_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

data "aws_iam_policy_document" "ecs_service_task_execution_policies" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }

  statement {
    effect = "Allow"
    actions = [
      "kms:Decrypt",
      "secretsmanager:GetSecretValue"
    ],
    resource = [
      "arn:aws:secretsmanager:${var.region}:${var.account_id}:secret:*",
      "arn:aws:kms:${var.region}:${var.account_id}:key/*"
    ]
  }

  statement {
    effect = "Allow"
    actions = [
      "ecr:GetAuthorizationToken",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resource = "*"
  }

  statement {
    effect = "Allow"
    actions = [
      "ecr:BatchGetImage",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchCheckLayerAvailability",
    ]

    resource = "*"

    condition {
      test = "StringEquals"
      variable = "aws:sourceVpce"
      values = data.terraform_remote_state.base.outputs.ecr_vpce_id
    }

    condition {
      test = "StringEquals"
      variable = "aws:sourceVpc"
      values = var.vpc_id
    }
  }
}
