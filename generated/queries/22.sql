select cntrycode,
  count(*) as numcust,
  sum(c_acctbal) as totacctbal
from (
    select substring(
        c_phone
        from 1 for 2
      ) as cntrycode,
      c_acctbal
    from CUSTOMER
    where substring(
        c_phone
        from 1 for 2
      ) in ('20', '40', '22', '30', '39', '42', '21')
      and c_acctbal > (
        select avg(c_acctbal)
        from CUSTOMER
        where c_acctbal > 0.00
          and substring(
            c_phone
            from 1 for 2
          ) in ('20', '40', '22', '30', '39', '42', '21')
      )
      and not exists (
        select *
        from ORDERS
        where o_custkey = c_custkey
      )
  ) as custsale
group by cntrycode
order by cntrycode;