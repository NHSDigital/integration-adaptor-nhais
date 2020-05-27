resource "aws_ecr_lifecycle_policy" "nhais_policy" {
  repository = aws_ecr_repository.nhais_ecr_repository.name 
  policy = jsonencode(
    {
      rules = [
        {
          rulePriority = 1
          description = "Expire images older than 14 days from PRs"
          selection = {
            tagStatus = "tagged"
            tagPrefixList = ["PR"]
            countType = "sinceImagePushed"
            countUnit = "days"
            countNumber = "14"
          }
          action = {
            type = "expire"
          }
        }
      ]
    }
  )
}