select sum(l_extendedprice * l_discount) as revenue
from LINEITEM
where l_shipdate >= datetime('1994-01-01')
	and l_shipdate < datetime('1995-01-01')
  and l_discount >= 0.05
  and l_discount <= 0.07
	and l_quantity < 24;
