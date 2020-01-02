package com.yicj.rpc.test;

import com.yicj.rpc.common.exception.RemotingCommmonCustomException;
import com.yicj.rpc.model.CommonCustomBody;
import com.yicj.rpc.model.RemotingTransporter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class NettyTransporterTest1 {
    public static final byte STUDENTS = 1;
    public static final byte TEACHER = 2;
    public static void main(String[] args) {
        StudentInfos infos = new StudentInfos();
        //学生信息
        RemotingTransporter studentsRemotingTransporter = RemotingTransporter.createRequestTransporter(STUDENTS, infos);
        //学生信息
        TeacherInfo info = new TeacherInfo();
        RemotingTransporter teacherRemotingTransporter = RemotingTransporter.createRequestTransporter(TEACHER, info);
    }

    @Data
    private static class StudentInfos implements CommonCustomBody {
        List<String> students = new ArrayList<String>();
        @Override
        public void checkFields() throws RemotingCommmonCustomException {
        }
    }

    @Data
    private static class TeacherInfo implements CommonCustomBody {
        String teacher = "";
        @Override
        public void checkFields() throws RemotingCommmonCustomException {
        }
    }
}
