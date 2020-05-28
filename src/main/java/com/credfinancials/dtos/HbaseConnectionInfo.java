package com.credfinancials.dtos;

import java.io.Serializable;

import static com.credfinancials.Constants.Constants.HBASE_MASTER_PORT;

/***
 * DTO to store hbase connection info.
 */
public class HbaseConnectionInfo implements Serializable {
    private String hbaseMaster;
    private String hbaseZookeeperQuoram;
    private String hbaseZookeeperClientPort;
    private String zookeeperZnodeParent;

    public HbaseConnectionInfo(String hbaseZookeeperQuoram) {
        this.hbaseMaster = hbaseZookeeperQuoram + ":" + HBASE_MASTER_PORT;
        this.hbaseZookeeperQuoram = hbaseZookeeperQuoram;
        this.hbaseZookeeperClientPort = "2181";
        this.zookeeperZnodeParent = "/hbase";
    }

    public HbaseConnectionInfo(String hbaseZookeeperQuoram, String hbaseZookeeperClientPort, String zookeeperZnodeParent) {
        this.hbaseMaster = hbaseZookeeperQuoram + ":" + HBASE_MASTER_PORT;
        this.hbaseZookeeperQuoram = hbaseZookeeperQuoram;
        this.hbaseZookeeperClientPort = hbaseZookeeperClientPort;
        this.zookeeperZnodeParent = zookeeperZnodeParent;
    }

    public String getHbaseMaster() {
        return hbaseMaster;
    }

    public void setHbaseMaster(String hbaseMaster) {
        this.hbaseMaster = hbaseMaster;
    }

    public String getHbaseZookeeperQuoram() {
        return hbaseZookeeperQuoram;
    }

    public void setHbaseZookeeperQuoram(String hbaseZookeeperQuoram) {
        this.hbaseZookeeperQuoram = hbaseZookeeperQuoram;
    }

    public String getHbaseZookeeperClientPort() {
        return hbaseZookeeperClientPort;
    }

    public void setHbaseZookeeperClientPort(String hbaseZookeeperClientPort) {
        this.hbaseZookeeperClientPort = hbaseZookeeperClientPort;
    }

    public String getZookeeperZnodeParent() {
        return zookeeperZnodeParent;
    }

    public void setZookeeperZnodeParent(String zookeeperZnodeParent) {
        this.zookeeperZnodeParent = zookeeperZnodeParent;
    }
}
