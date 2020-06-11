select sum(l_extendedprice * l_discount)
from LINEITEM,
  PARTSUPP,
  PART
where l_partkey = ps_partkey
  and l_suppkey = ps_suppkey
  and ps_partkey = p_partkey
  and p_brand <> 'Brand#44'
  and ps_supplycost < 50.0;