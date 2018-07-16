package com.hacklympics.api.snapshot;

import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hacklympics.api.communication.Response;
import com.hacklympics.api.user.Student;
import com.hacklympics.api.utility.NetworkUtils;

public class Snapshot {
    
    private final int examID;
    private final String studentUsername;
    private final String snapshot;
    private final String timestamp;
    
    public Snapshot(int examID, String studentUsername, String snapshot, String timestamp) {
        this.examID = examID;
        this.studentUsername = studentUsername;
        this.snapshot = snapshot;
        this.timestamp = timestamp;
    }
    
    
    public static Response create(int courseID, int examID, String student, String b64image) {
        String uri = String.format("course/%d/exam/%d/snapshot/create", courseID, examID);
        
        JsonObject json = new JsonObject();
        json.addProperty("student", student);
        json.addProperty("image", b64image);
        
        return new Response(NetworkUtils.post(uri, json.toString()));
    }
    
    public static Response adjustParam(int courseID, int examID, List<Student> students, double quality, int frequency) {
        String uri = String.format("course/%d/exam/%d/snapshot/adjust_param", courseID, examID);
        
        JsonArray studentsJsonArray = new JsonArray();
        for (Student student : students) {
            studentsJsonArray.add(student.getUsername());
        }
        
        JsonObject json = new JsonObject();
        json.addProperty("quality", quality);
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
    
    public String getSnapshot() {
        return this.snapshot;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
    
}
