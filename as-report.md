# Software Architecture Project Report

## Performance during the process of answering a quiz

### Architecture

![Performance Architecture](report-resources/performance-architecture.png)


### Scenarios

**1)** After the login and within a period of 10s, <num> student users make a request stochasticly by inserting a code to get access to the quiz. The Quizzes Tutor, in normal operation, sends the quiz's questions to each user in less than <x> seconds/miliseconds.
**2)** After accessing the quiz, <num> students wait until the of the timer to start the quiz. All the students answer to the questions following a normal distribution and in the end conclude the quiz. The quizzes tutor receives the answers and the and final submissions and saves, for each student, this information in a database in less than <x> seconds/miliseconds.
  

### Tests

#### [Get quiz with code](backend/jmeter/answer/get-quizzes.jmx)

* The teacher logs in, creates the questions and makes the quiz
* 1000 students log in
* After everyone logs in, the teacher populates the quiz
* Then each student enters the code to get the quiz

**Results:**

This first test tried to simulate a real scenario where multiple students insert the code for the quiz at a random time between 1s and 10s. (Bigger intervals where tested and they gave similar results)

* 300 students
![300 Students at a random time 1s-10s](report-resources/performance-getquiz-300_rt.png)
* 600 students
![600 Students at a random time 1s-10s](report-resources/performance-getquiz-600_rt.png)
* 1000 students
![1000 Students at a random time 1s-10s](report-resources/performance-getquiz-1000_rt.png)

**Conclusions:** With this tests we can conclude that at least until 1000 students, the average time taken to get the quiz is independent of the number of students.


This second test tried to simulate a limit scenario where all the students insert the code at the same time (This test uses a synchronizing timer to make sure that all threads are created before the get quiz sample starts)

* 300 students
![300 Students at the same time](report-resources/performance-getquiz-300_st.png)
* 600 students
![600 Students at the same time](report-resources/performance-getquiz-600_st.png)
* 1000 students
![1000 Students at the same time](report-resources/performance-getquiz-1000_st.png)


**Conclusions:** This time we can see that the average time it takes to get a quiz is proportional to the number of students. 


#### [Quiz answering with code](backend/jmeter/answer/quiz-answer-with-code.jmx)

* The teacher logs in, creates the questions and makes the quiz
* 1000 students log in
* After everyone logs in, the teacher populates the quiz
* Then each student enters the code to get the quiz
* After everyone gets the quiz, they start at the same time answering the questions
* These questions are answered in a time according to a normal distribution, with average of 35 seconds and deviation of 5 seconds
* After the students have answered all the questions, the teacher writes the answers

**Results:**

This test corresponds to the scenario 2.

* 300 students
![300 Students at a random time 1s-10s](report-resources/performance-answerquiz-300_normt.png)
* 600 students
![600 Students at a random time 1s-10s](report-resources/performance-answerquiz-600_normt.png)
* 1000 students
![1000 Students at a random time 1s-10s](report-resources/performance-answerquiz-1000_normt.png)

**Conclusions:** With this test we can see that the process of answering a quiz is independent of the number of students (at least until 1000 students) and it's fastest than getting a quiz.


In this test, we tried to simulate an unrealistic scenario where all the students get the quiz and answer the questions at the same time, without having time to "think". We pretended to stress test the system to see how it would cope.

* 300 students
![300 Students at the same time](report-resources/performance-answerquiz-300_st.png)
* 600 students
![600 Students at the same time](report-resources/performance-answerquiz-600_st.png)
* 1000 students
![1000 Students at the same time](report-resources/performance-answerquiz-1000_st.png)

**Conclusions:** In this second test, we can see that the average times in the process of answering a quiz are proportional to the number of students but not as much as in the get quiz test. This time, even with 1000 students answering at the same time, the average is below 1s. So with all this tests, we can conclude that the system that already exists achieves the performance desired to fulfill the requirement of having 1000 students answering a quiz using a code, previously populated with answers.
