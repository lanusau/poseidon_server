package com.untd.database.poseidon.data;

/**
 * MyBatis mapper to update PSD_SERVER table
 *
 */
public interface ServerMapper {
    
    /**
     * Log heartbeat
     * 
     * @param serverId server ID
     * @return rows updated
     */
    int heartbeat(Integer serverId);
    
}