package com.untd.database.poseidon.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import com.untd.database.poseidon.model.database.Script;

@Transactional
public class ScriptDao extends HibernateDao<Script,Integer>{

	public ScriptDao() {
		super(Script.class);		
	}
	
	/**
	 * Get a list of active scripts for this server
	 * @param serverId server id
	 * @return array list of scripts
	 */
	@SuppressWarnings("unchecked")
	public List<Script> getActiveScriptList(int serverId) {
		return getSessionFactory().getCurrentSession()
				.createQuery(
						"from Script s "
						+ " where statusCode = 'A'" + 
						"   and ( " +  
						"      exists ( " + 
						"        select ' ' from ScriptTarget st, Target t " + 
						"        where st.scriptId = s.scriptId " + 
						"        and st.targetId = t.targetId " + 
						"        and t.serverId = :serverId" + 
						"      ) " + 
						"      or " + 
						"      exists ( " + 
						"        select ' ' from ScriptGroup sg, TargetGroupAssignment tg, Target t " + 
						"        where sg.scriptId = s.scriptId " + 
						"        and sg.targetGroupId = tg.targetGroupId " + 
						"        and tg.targetId = t.targetId " + 
						"        and t.serverId = :serverId" + 
						"      ) " + 
						"    )")
				.setInteger("serverId", serverId).list();
	}
	
	public Script findOneWithDependencies(Integer scriptId) {
		Script script = findOne(scriptId);
		Hibernate.initialize(script.getScriptNotifications());
		Hibernate.initialize(script.getScriptPersonNotifications());
		return script;
	}
}
