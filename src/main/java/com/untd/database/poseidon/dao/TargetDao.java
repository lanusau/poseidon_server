package com.untd.database.poseidon.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.Target;



public class TargetDao extends HibernateDao<Target,Integer>{
	
	public TargetDao() {
		super(Target.class);
	}

	@SuppressWarnings("unchecked")
	public  List<Target> getScriptTargets(Script script, int serverId) {
		Query directTargets = getSessionFactory().getCurrentSession()
				.createQuery("from Target t "
						+ " where t.statusCode = 'A' "
						+ " and t.serverId = :serverId"
						+ " and exists ("
						+ "   select '' from ScriptTarget st"
						+ "   where t.targetId = st.targetId "
						+ "   and st.scriptId = :scriptId"
						+ ")")
				.setInteger("scriptId", script.getScriptId())
				.setInteger("serverId", serverId);
		Query targetsThroughGroups = getSessionFactory().getCurrentSession()
				.createQuery("from Target t "
						+ " where t.statusCode = 'A' "
						+ " and t.serverId = :serverId"
						+ " and exists ("
						+ "   select '' from ScriptGroup sg, TargetGroupAssignment a"
						+ "   where t.targetId = a.targetId "
						+ "   and a.targetGroupId = sg.targetGroupId"
						+ "   and sg.scriptId = :scriptId"
						+ ")")
				.setInteger("scriptId", script.getScriptId())
				.setInteger("serverId", serverId);
		Set<Target> targetList = new HashSet<Target>(directTargets.list());
		targetList.addAll(targetsThroughGroups.list());
		return new ArrayList<Target>(targetList);
	}
}
