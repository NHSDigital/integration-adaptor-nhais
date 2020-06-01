output "loadbalancer_sg_id" {
  value =  var.enable_load_balancing ? aws_securiy_group.service_lb_sg.id : null
}

output "loadbalancer_dns_name" {
  value =  var.enable_load_balancing ? aws_lb.service_load_balancer.dns_name : null
}
