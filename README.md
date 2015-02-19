We are provided with a set of tasks. Each task ti has a profit pi associated with it. If pi>0 then we make profit while if pi<0, we lose money. To make things harder, we are also provided with some constraints within tasks. Intuitively, model the problem as a graph where each task is a node. An edge exists between tasks ti and tj if tj is a prerequisite of ti. In other words, if we want to do ti, we must also do tj. The objective is to identify a subset of tasks such that the profit is maximized and all the inter-dependencies are satisfied.

Input Format:   
Input is expected as a single file. The first line provides the total number of tasks. The subsequent line describe each task per line as a tuple \<task id, task profit, list of projects dependent on>. The last parameter can be empty!

Sample Input:   
6   
1,10,3,4    
2,20,4    
3,-5    
4,-3    
5,10,6    
6,-11   

Corresponding Output:   
1   
2   
3   
4   
