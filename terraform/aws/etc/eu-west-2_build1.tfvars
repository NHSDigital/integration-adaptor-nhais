environment = "build1"
base_cidr_block = "10.11.0.0/16"

nhais_service_desired_count = 2
nhais_service_container_port = 80
nhais_service_launch_type = "FARGATE"
cluster_container_insights = "enabled"

# nhais_image_name = "067756640211.dkr.ecr.eu-west-2.amazonaws.com/nhais:feature-java-26-dd577d0"
# build_id = "feature-java-26-dd577d0"
nhais_image_name = "067756640211.dkr.ecr.eu-west-2.amazonaws.com/nhais:PR-39-30-863332c"
build_id = "PR-39-30-863332c"

