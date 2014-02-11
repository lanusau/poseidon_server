package com.untd.database.poseidon.data;

import java.util.List;
import com.untd.database.poseidon.Notification;

public interface ScriptMapper {
	
    /**
     * Select one script
     */
    Script select(Integer scriptId);

    /**
     * Select all Active scripts
     */
    List<Script> selectActive(Integer serverId);
    
    /**
     * Select all Inactive scripts
     */
    List<Script> selectInactive(Integer serverId);    
    
    /**
     * Select group notifications assigned to this script
     * @param scriptId
     * @return
     */
    List<Notification> selectGroupNotifications(Integer scriptId);
    
    /**
     * Select person notification attached to a script
     * @param scriptId
     * @return
     */
    List<Notification> selectPersonNotifications(Integer scriptId);

}