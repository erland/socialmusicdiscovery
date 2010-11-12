select distinct a.titlesort 
from 
	contributors as c, 
	contributor_album as ca, 
	albums as a 
where c.namesort = "a dominique" and c.id = ca.contributor and ca.album = a.id;
