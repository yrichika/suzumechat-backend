package com.example.suzumechat.service.channel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String channelId;
    private String hostIdHashed; // Host Id

    // for channel management
    private String channelTokenId;
    private String channelNameEnc;
    private String hostChannelTokenHashed;
    private String loginChannelTokenHashed;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] loginChannelTokenEnc;
    private String clientChannelTokenHashed;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] clientChannelTokenEnc;

    @Column(nullable = true)
    private String secretKeyEnc;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
