create view REVENUE0 (supplier_no, total_revenue) as
select l_suppkey,
  sum(l_extendedprice * (1 - l_discount))
from LINEITEM
where l_shipdate >= date '1997-07-01'
  and l_shipdate < date '1997-07-01' + interval '3' month
group by l_suppkey;
select s_suppkey,
  s_name,
  s_address,
  s_phone,
  total_revenue
from SUPPLIER,
  REVENUE0
where s_suppkey = supplier_no
  and total_revenue = (
    select max(total_revenue)
    from REVENUE0
  )
order by s_suppkey;
drop view REVENUE0;