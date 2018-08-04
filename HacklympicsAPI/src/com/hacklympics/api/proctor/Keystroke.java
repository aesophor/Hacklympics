package com.hacklympics.api.proctor;

import com.google.gson.JsonArray;
import java.util.List;
import com.google.gson.JsonObject;
import com.hacklympics.api.communication.Response;
import com.hacklympics.api.user.Student;
import com.hacklympics.api.utility.NetworkUtils;

public class Keystroke implements ProctorMedium {
    
    private final int examID;
    private final String studentUsername;
    private final List<String> history;
    private final String timestamp;
    
    public Keystroke(int examID, String studentUsername, List<String> history, String timestamp) {
        this.examID = examID;
        this.studentUsername = studentUsername;
        this.history = history;
        this.timestamp = timestamp;
    }
    
    
    public static Response sync(int courseID, int examID, String student, List<String> history) {
        String uri = String.format("course/%d/exam/%d/keystroke/sync", courseID, examID);
        
        JsonArray historyJsonArray = new JsonArray();
        for (String moment : history) {
            historyJsonArray.add(moment);
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("student", student);
        json.add("history", historyJsonArray);
        
        return new Response(NetworkUtils.post(uri, json.toString()));
    }
    
    public static Response adjustParam(int courseID, int examID, List<Student> students, int frequency) {
        String uri = String.format("course/%d/exam/%d/keystroke/adjust_param", courseID, examID);
        
        JsonArray studentsJsonArray = new JsonArray();
        for (Student student : students) {
            studentsJsonArray.add(student.getUsername());
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("frequency", frequency);
        json.add("students", studentsJsonArray);
        
        return new Response(NetworkUtils.post(uri, json.toString()));
    }
    
    
    public int getExamID() {
        return this.examID;
    }
    
    public String getStudentUsername() {
        return this.studentUsername;
    }
    
    public List<String> getHistory() {
        return this.history;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
    
}