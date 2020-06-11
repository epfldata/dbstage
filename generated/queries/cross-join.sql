select max(abs(s1.s_acctbal - s2.s_acctbal))
from SUPPLIER s1
cross join SUPPLIER s2;