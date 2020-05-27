data "terraform_remote_state" "account" {
  backend = "s3"
  
  config = {
    bucket = var.tf_state_bucket
    key = "${var.project}-${var.environment}-account.tfstate"
    region = var.region
  }
}