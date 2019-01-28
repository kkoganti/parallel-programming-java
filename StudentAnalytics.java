package edu.coursera.parallel;

import java.util.*;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple wrapper class for various analytics methods.
 */
public final class StudentAnalytics {
    /**
     * Sequentially computes the average age of all actively enrolled students
     * using loops.
     *
     * @param studentArray Student data for the class.
     * @return Average age of enrolled students
     */
    public double averageAgeOfEnrolledStudentsImperative(
            final Student[] studentArray) {
        List<Student> activeStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (s.checkIsCurrent()) {
                activeStudents.add(s);
            }
        }

        double ageSum = 0.0;
        for (Student s : activeStudents) {
            ageSum += s.getAge();
        }

        return ageSum / (double) activeStudents.size();
    }

    /**
     * TODO compute the average age of all actively enrolled students using
     * parallel streams. This should mirror the functionality of
     * averageAgeOfEnrolledStudentsImperative. This method should not use any
     * loops.
     *
     * @param studentArray Student data for the class.
     * @return Average age of enrolled students
     */
    private class collectAvgAge implements DoubleConsumer {
        private int count=0;
        private Double sum=0.0;


        public void accept(double value) {
          count++;
          sum += value;
        }

        public void combine(collectAvgAge avgAge){
            count = count + avgAge.count;
            sum = sum + avgAge.sum;

        }
        public  Double avgCalculate(){
            return count > 0 ? (sum/count ): 0;
        }
    }
    public double averageAgeOfEnrolledStudentsParallelStream(
            final Student[] studentArray) {
        //throw new UnsupportedOperationException();
        collectAvgAge avgAge =  Stream.of(studentArray)
                        .parallel()
                        .filter(s->s.checkIsCurrent())
                        .map(s->s.getAge())
                        .collect(collectAvgAge::new,collectAvgAge::accept,collectAvgAge::combine);
        return avgAge.avgCalculate();

    }

    /**
     * Sequentially computes the most common first name out of all students that
     * are no longer active in the class using loops.
     *
     * @param studentArray Student data for the class.
     * @return Most common first name of inactive students
     */
    public String mostCommonFirstNameOfInactiveStudentsImperative(
            final Student[] studentArray) {
        List<Student> inactiveStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (!s.checkIsCurrent()) {
                inactiveStudents.add(s);
            }
        }

        Map<String, Integer> nameCounts = new HashMap<String, Integer>();

        for (Student s : inactiveStudents) {
            if (nameCounts.containsKey(s.getFirstName())) {
                nameCounts.put(s.getFirstName(),
                        new Integer(nameCounts.get(s.getFirstName()) + 1));
            } else {
                nameCounts.put(s.getFirstName(), 1);
            }
        }

        String mostCommon = null;
        int mostCommonCount = -1;
        for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
            if (mostCommon == null || entry.getValue() > mostCommonCount) {
                mostCommon = entry.getKey();
                mostCommonCount = entry.getValue();
            }
        }

        return mostCommon;
    }

    /**
     * TODO compute the most common first name out of all students that are no
     * longer active in the class using parallel streams. This should mirror the
     * functionality of mostCommonFirstNameOfInactiveStudentsImperative. This
     * method should not use any loops.
     *
     * @param studentArray Student data for the class.
     * @return Most common first name of inactive students
     */
    private class mostCommonFirstNameclass implements IntConsumer{
        private int maxAge=0;

        @Override
        public void accept(int value) {

        }
    }
    public String mostCommonFirstNameOfInactiveStudentsParallelStream(
            final Student[] studentArray) {
        //throw new UnsupportedOperationException();
        Map<String,Long>  firstNamecountsMap = Stream.of(studentArray)
                            .parallel()
                            .filter(s->!s.checkIsCurrent())
                            .collect(Collectors.groupingBy(Student::getFirstName
                                                            ,Collectors.counting()));

        Map.Entry<String,Long> maxFirstNameMapEntry = firstNamecountsMap.entrySet().stream().parallel()
                                 .max(Map.Entry.comparingByValue()).get();
        String maxFirstName = maxFirstNameMapEntry.getKey();

        return maxFirstName;
    }

    /**
     * Sequentially computes the number of students who have failed the course
     * who are also older than 20 years old. A failing grade is anything below a
     * 65. A student has only failed the course if they have a failing grade and
     * they are not currently active.
     *
     * @param studentArray Student data for the class.
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20Imperative(
            final Student[] studentArray) {
        int count = 0;
        for (Student s : studentArray) {
            if (!s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) {
                count++;
            }
        }
        return count;
    }

    /**
     * TODO compute the number of students who have failed the course who are
     * also older than 20 years old. A failing grade is anything below a 65. A
     * student has only failed the course if they have a failing grade and they
     * are not currently active. This should mirror the functionality of
     * countNumberOfFailedStudentsOlderThan20Imperative. This method should not
     * use any loops.
     *
     * @param studentArray Student data for the class.
     * @return Number of failed grades from students older than 20 years old.
     */
    public int countNumberOfFailedStudentsOlderThan20ParallelStream(
            final Student[] studentArray) {

        int cntFailedStudents =  Stream.of(studentArray).parallel()
                                .filter(s -> !s.checkIsCurrent() && s.getAge()>20 && s.getGrade() <65)
                                .collect(Collectors.reducing(0,s->1,Integer::sum));
        return cntFailedStudents;

    }

    public static void main(String args[]){

        System.out.println("test");
        Student s1 = new Student("kranthi","koganti",30.5,1,false);
        Student s2 = new Student("nivi","kad",28,1,false);
        Student s3 = new Student("kranthi","kog",30.5,1,false);

        Student[] studArray = new Student[3];
        studArray[0]=s1;
        studArray[1]=s2;
        studArray[2]=s3;

         StudentAnalytics stAnalytics = new StudentAnalytics();
        Double averageAge = stAnalytics.averageAgeOfEnrolledStudentsParallelStream(studArray);
        System.out.println(averageAge);
        String firstName =stAnalytics.mostCommonFirstNameOfInactiveStudentsParallelStream(studArray);
        System.out.println(firstName);
    }
}
