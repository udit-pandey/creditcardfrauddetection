# lambdaArchitecture
This implementation focuses more on the engineering part where we focus on completing the transaction within few seconds and use different tools available in the big data ecosystem to solve the problem. Ideally, ML is used to classify a transaction as FRAUD/GENUINE, however, here we make use of some predefined rules. Leaving this part, all other components remain the same. <br/>

The data from the several POS systems will flow inside the architecture through a queuing system like Kafka. The POS data from Kafka will be consumed by the streaming data processing framework to identify the authenticity of the transactions. Once the POS data enters into the Stream processing layer, it is assessed based on some parameters defined by the rules. Only, when the results are positive for these rules, the transaction is allowed to complete.Following are the rules:<br/>
1. Upper Control Limit: Suppose you have a past record of making transactions with an average amount of $20,000, and one day the system observes a transaction of $200,000 through your card. This can be a possible case of fraud. In such cases, the cardholder receives a call from the credit card company executives to validate the transaction. UCL is derived using the following formula:<br/>
UCL= (Moving Average)+3×(Standard Deviation)<br/><br/>

2. Credit score of each member:This is a straightforward rule, where we have a member_score table in which member ids and their respective scores are available. These scores are updated from a third-party service. If the score is less than 200, that member’s transaction is rejected as he/she could be a defaulter. This rule simply defines the financial reputation of each customer.<br/><br/>

3. Zip code distance: The purpose of this rule is to keep a check on the distance between the card owner's current and last transaction location with respect to time. If the distance between the current transaction and the last transaction location with respect to time is greater than a particular threshold, then this raises suspicion on the authenticity of the transaction.<br/><br/>

Credit score is updated in an amazon RDS instance and is ingested into HDFS using Sqoop. This ingested data is stored as a Hive-Hbase integrated table.<br/>
New transactions are queued in our kafka instance and are consumed using Spark streaming. Also, once the transaction is classified into GENUINE/FRAUD, we store the transaction in Hive-HBase integrated table.<br/>
Also, this Hive-HBase integrated table is updated for the member score for each card on a regular basis. Finally, it is also used for data visualization by customer service executives. <br/>



