resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.base_vpc.id
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-igw"
  })
}

resource "aws_eip" "nat_gw_eip" {
  vpc = true
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-nat_gw_eip"
  })
}

resource "aws_nat_gateway" "nat_gw" {
  subnet_id = aws_subnet.nat_gw_subnet.id
  allocation_id = aws_eip.nat_gw_eip.id

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-nat_gw_eip"
  })
}

resource "aws_subnet" "nat_gw_subnet" {
  vpc_id = aws_vpc.base_vpc.id
  cidr_block = cidrsubnet(aws_vpc.base_vpc.cidr_block,3,4)

  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-nat_gw_subnet"
  })
}

resource "aws_route_table" "public_igw" { 
  vpc_id = aws_vpc.base_vpc.id
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-public_rt"
  })
}

resource "aws_route" "route_public_to_igw" {
  route_table_id = aws_route_table.public_igw.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.igw.id
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.base_vpc.id
  tags = merge(local.default_tags, {
    Name = "${local.resource_prefix}-private_rt"
  })
}

resource "aws_route" "route_private_to_nat_gw" {
  route_table_id = aws_route_table.private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id = aws_nat_gateway.nat_gw.id
}

resource "aws_route_table_association" "public_route_public_subnet" {
  subnet_id = aws_subnet.nat_gw_subnet.id
  route_table_id = aws_route_table.public_igw.id 
  
}

resource "aws_route_table_association" "private_route_base_subnet" {
  subnet_id = aws_subnet.base_subnet.id
  route_table_id = aws_route_table.private.id
}