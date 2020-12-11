# Software Architecture Project Report

## Performance during the process of answering a quiz

### Architecture

**TODO**: Try making process more data driven and split quizAnswerItem
![Performance Architecture](report-resources/performance-architecture.jpg)


### Cenarios

To answer an available quizz, 1000 student users start the quiz, submit answers to the questions and conclude the quiz. The quizzes tutor saves each answer and the conclusion with a maximum latency of 200ms, with a throughput of 1000/sec, with 0% of miss rate.

### Tests

[Quiz answering with code](backend/jmeter/answer/quiz-answer-with-code.jmx)

* Logs in as teacher, creates a quiz and populates it
* 1000 students log in and get the quiz by code
* After everyone gets the quiz, they start at the same time and answer the questions
* These questions are answered in a time according to a normal distribution, with average of 35 seconds and deviation of 5 seconds
* After the students have answered all the questions, the teacher writes the answers

This tests yielded a average sample time of 12 ms.
![Performance Test Results](report-resources/performance-test1.png)

If the test does not use the functionality of populating the quiz answers before the students answer, (using 600 students) the average sample time increases to 13 seconds.