resource "aws_dynamodb_table" "nhais_dynamodb" {
  name = "${local.resource_prefix}-dynamodb"
  hash_key = "key"
  read_capacity = 5
  write_capacity = 5

  attribute {
    name = "key"
    type = "S"
  }

  tags = merge(local.default_tags,{
    Name = "${local.resource_prefix}-dynamodb"
  })
}