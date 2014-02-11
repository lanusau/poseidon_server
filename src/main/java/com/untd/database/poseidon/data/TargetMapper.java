package com.untd.database.poseidon.data;

import java.util.List;

public interface TargetMapper {

    Target select(Integer targetId);

    List<Target> selectScriptTargets(Integer scriptId);

}