data "terraform_remote_state" "base" {
  backend = "s3"
  
  config = {
    bucket = var.tf_state_bucket
    key = "${project}-${environment}-base.tfstate"
    region = var.region
  }
}