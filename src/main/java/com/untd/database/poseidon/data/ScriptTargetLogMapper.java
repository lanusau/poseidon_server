package com.untd.database.poseidon.data;

public interface ScriptTargetLogMapper {


    int insert(ScriptTargetLog record);
    int update(ScriptTargetLog record);
    int insertRow(ScriptTargetRowLog record);
    int insertColumn(ScriptTargetColLog record);
}