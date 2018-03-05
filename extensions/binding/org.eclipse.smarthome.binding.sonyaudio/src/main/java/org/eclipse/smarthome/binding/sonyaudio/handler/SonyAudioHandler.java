/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.sonyaudio.handler;

import static org.eclipse.smarthome.binding.sonyaudio.SonyAudioBindingConstants.*;

import org.eclipse.smarthome.binding.sonyaudio.SonyAudioBindingConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.smarthome.binding.sonyaudio.internal.SonyAudioEventListener;
import org.eclipse.smarthome.binding.sonyaudio.internal.protocol.SonyAudioConnection;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SonyAudioHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author David Åberg - Initial contribution
 */
abstract class SonyAudioHandler extends BaseThingHandler implements SonyAudioEventListener {

    private final Logger logger = LoggerFactory.getLogger(SonyAudioHandler.class);

    private SonyAudioConnection connection;

    private ScheduledFuture<?> connectionCheckerFuture;
    private ScheduledFuture<?> refreshJob;

    private int currentRadioStation = 0;
    private Map<Integer,String> input_zone = new HashMap<Integer,String>();

    public SonyAudioHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (connection == null) {
            logger.debug("Thing not yet initialized!");
            return;
        }

        String id = channelUID.getId();

        logger.debug("Handle command {} {}", channelUID, command);

        try {
            switch (id) {
                case CHANNEL_POWER:
                case CHANNEL_MASTER_POWER:
                    if (command instanceof RefreshType) {
                      updateState(channelUID, connection.getPower() ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                      connection.setPower(setPowerCommand(command));
                    }
                    break;
                case CHANNEL_ZONE1_POWER:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getPower(1) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setPower(setPowerCommand(command), 1);
                    }
                    break;
                case CHANNEL_ZONE2_POWER:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getPower(2) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setPower(setPowerCommand(command), 2);
                    }
                    break;
                case CHANNEL_ZONE3_POWER:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getPower(3) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setPower(setPowerCommand(command), 3);
                    }
                    break;
                case CHANNEL_ZONE4_POWER:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getPower(4) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setPower(setPowerCommand(command), 4);
                    }
                    break;
                case CHANNEL_INPUT:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, inputSource(connection.getInput()));
                    }
                    if (command instanceof StringType) {
                        connection.setInput(setInputCommand(command));
                    }
                    break;
                case CHANNEL_ZONE1_INPUT:
                    if (command instanceof RefreshType) {
                        input_zone.put(1,connection.getInput(1));
                        updateState(channelUID, new StringType(input_zone.get(1)));
                    }
                    if (command instanceof StringType) {
                        connection.setInput(setInputCommand(command), 1);
                    }
                    break;
                case CHANNEL_ZONE2_INPUT:
                    if (command instanceof RefreshType) {
                        input_zone.put(2,connection.getInput(2));
                        updateState(channelUID, new StringType(input_zone.get(2)));
                    }
                    if (command instanceof StringType) {
                        connection.setInput(setInputCommand(command), 2);
                    }
                    break;
                case CHANNEL_ZONE3_INPUT:
                    if (command instanceof RefreshType) {
                        input_zone.put(3,connection.getInput(3));
                        updateState(channelUID, new StringType(input_zone.get(3)));
                    }
                    if (command instanceof StringType) {
                        connection.setInput(setInputCommand(command), 3);
                    }
                    break;
                case CHANNEL_ZONE4_INPUT:
                    if (command instanceof RefreshType) {
                        input_zone.put(4,connection.getInput(4));
                        updateState(channelUID, new StringType(input_zone.get(4)));
                    }
                    if (command instanceof StringType) {
                        connection.setInput(setInputCommand(command), 4);
                    }
                    break;
                case CHANNEL_VOLUME:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getVolume() / 100.0));
                    }
                    if (command instanceof DecimalType) {
                        connection.setVolume(setVolumeCommand(command));
                    }
                    break;
                case CHANNEL_ZONE1_VOLUME:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getVolume(1) / 100.0));
                    }
                    if (command instanceof DecimalType) {
                        connection.setVolume(setVolumeCommand(command), 1);
                    }
                    break;
                case CHANNEL_ZONE2_VOLUME:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getVolume(2) / 100.0));
                    }
                    if (command instanceof DecimalType) {
                        connection.setVolume(setVolumeCommand(command), 2);
                    }
                    break;
                case CHANNEL_ZONE3_VOLUME:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getVolume(3) / 100.0));
                    }
                    if (command instanceof DecimalType) {
                        connection.setVolume(setVolumeCommand(command), 3);
                    }
                    break;
                case CHANNEL_ZONE4_VOLUME:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getVolume(4) / 100.0));
                    }
                    if (command instanceof DecimalType) {
                        connection.setVolume(setVolumeCommand(command), 4);
                    }
                    break;
                case CHANNEL_MUTE:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getMute() ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setMute(setMuteCommand(command));
                    }
                    break;
                case CHANNEL_ZONE1_MUTE:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getMute(1) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setMute(setMuteCommand(command), 1);
                    }
                    break;
                case CHANNEL_ZONE2_MUTE:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getMute(2) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setMute(setMuteCommand(command), 2);
                    }
                    break;
                case CHANNEL_ZONE3_MUTE:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getMute(3) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setMute(setMuteCommand(command), 3);
                    }
                    break;
                case CHANNEL_ZONE4_MUTE:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getMute(4) ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setMute(setMuteCommand(command), 4);
                    }
                    break;
                case CHANNEL_MASTER_SOUND_FIELD:
                case CHANNEL_SOUND_FIELD:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new StringType(connection.getSoundField()));
                    }
                    if (command instanceof StringType) {
                        connection.setSoundField(setSoundFieldCommand(command));
                    }
                    break;
                case CHANNEL_MASTER_PURE_DIRECT:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getPureDirect() ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setPureDirect(setPureDirectCommand(command));
                    }
                    break;
                case CHANNEL_CLEAR_AUDIO:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, connection.getClearAudio() ? OnOffType.ON : OnOffType.OFF);
                    }
                    if (command instanceof OnOffType) {
                        connection.setClearAudio(setClearAudioCommand(command));
                    }
                    break;
                case CHANNEL_RADIO_FREQ:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(connection.getRadioFreq() / 1000000.0));
                    }
                    break;
                case CHANNEL_RADIO_STATION:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new DecimalType(currentRadioStation));
                    }
                    if (command instanceof DecimalType) {
                        currentRadioStation = ((DecimalType) command).intValue();
                        String radioCommand = setRadioStationCommand(currentRadioStation);

                        for(int i=1; i<=4; i++){
                          String input = input_zone.get(i);
                          if (input != null && input.startsWith("radio:fm")) {
                              connection.setInput(radioCommand, i);
                          }
                        }
                    }
                    break;
                case CHANNEL_RADIO_SEEK_STATION:
                    if (command instanceof RefreshType) {
                        updateState(channelUID, new StringType(""));
                    }
                    if (command instanceof StringType) {
                        switch (((StringType) command).toString()) {
                            case "fwdSeeking":
                                connection.radioSeekFwd();
                                break;
                            case "bwdSeeking":
                                connection.radioSeekBwd();
                                break;
                        }

                    }
                    break;
                default:
                    logger.error("Channel {} not supported!", id);
            }
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    public boolean setPowerCommand(Command command) {
      return ((OnOffType) command) == OnOffType.ON;
    }

    abstract public String setInputCommand(Command command);

    public int setVolumeCommand(Command command){
      return ((DecimalType) command).intValue();
    }

    public boolean setMuteCommand(Command command){
      return ((OnOffType) command) == OnOffType.ON;
    }

    public String setSoundFieldCommand(Command command){
      return ((StringType) command).toString();
    }

    public boolean setPureDirectCommand(Command command) {
      return ((OnOffType) command) == OnOffType.ON;
    }

    public boolean setClearAudioCommand(Command command){
      return ((OnOffType) command) == OnOffType.ON;
    }

    public String setRadioStationCommand(int currentRadioStation){
      return "radio:fm?contentId=" + currentRadioStation;
    }

    @Override
    public void initialize() {
        Configuration config = getThing().getConfiguration();
        String ipAddress = (String) config.get(SonyAudioBindingConstants.HOST_PARAMETER);
        String path = (String) config.get(SonyAudioBindingConstants.SCALAR_PATH_PARAMETER);
        Object port_o = config.get(SonyAudioBindingConstants.SCALAR_PORT_PARAMETER);
        int port;
        if (port_o instanceof BigDecimal) {
            port = ((BigDecimal) port_o).intValue();
        } else {
            port = (int) port_o;
        }

        Object refresh_o = config.get(SonyAudioBindingConstants.REFRESHINTERVAL);
        int refresh = 0;
        if (refresh_o instanceof BigDecimal) {
            refresh = ((BigDecimal) refresh_o).intValue();
        } else if (refresh_o instanceof Integer) {
            refresh = (int) refresh_o;
        }

        try {
            connection = new SonyAudioConnection(ipAddress, port, path, this);

            connection.connect(scheduler);

            // Start the connection checker
            Runnable connectionChecker = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!connection.checkConnection()) {
                            updateStatus(ThingStatus.OFFLINE);
                        }
                    } catch (Exception ex) {
                        logger.warn("Exception in check connection to @{}. Cause: {}", connection.getConnectionName(),
                                ex.getMessage());

                    }
                }
            };
            connectionCheckerFuture = scheduler.scheduleWithFixedDelay(connectionChecker, 1, 10, TimeUnit.SECONDS);

            // Start the status updater
            startAutomaticRefresh(refresh);
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (connectionCheckerFuture != null) {
            connectionCheckerFuture.cancel(true);
        }
        if (refreshJob != null) {
            refreshJob.cancel(true);
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void updateConnectionState(boolean connected) {
        if (connected) {
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void updateInputSource(int zone, String source) {
        switch (zone) {
            case 0:
                updateState(SonyAudioBindingConstants.CHANNEL_INPUT, inputSource(source));
                break;
            case 1:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE1_INPUT, inputSource(source));
                break;
            case 2:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE2_INPUT, inputSource(source));
                break;
            case 3:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE3_INPUT, inputSource(source));
                break;
            case 4:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE4_INPUT, inputSource(source));
                break;
        }
    }

    abstract public StringType inputSource(String input);

    @Override
    public void updateBroadcastFreq(int freq) {
        updateState(SonyAudioBindingConstants.CHANNEL_RADIO_FREQ, new DecimalType(freq / 1000000.0));
    }

    @Override
    public void updateCurrentRadioStation(int radioStation) {
        currentRadioStation = radioStation;
        updateState(SonyAudioBindingConstants.CHANNEL_RADIO_STATION, new DecimalType(currentRadioStation));
    }

    @Override
    public void updateSeekStation(String seek) {
        updateState(SonyAudioBindingConstants.CHANNEL_RADIO_SEEK_STATION, new StringType(seek));
    }

    @Override
    public void updateVolume(int zone, int volume) {
        switch (zone) {
            case 0:
                updateState(SonyAudioBindingConstants.CHANNEL_VOLUME, new DecimalType(volume / 100.0));
                break;
            case 1:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE1_VOLUME, new DecimalType(volume / 100.0));
                break;
            case 2:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE2_VOLUME, new DecimalType(volume / 100.0));
                break;
            case 3:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE3_VOLUME, new DecimalType(volume / 100.0));
                break;
            case 4:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE4_VOLUME, new DecimalType(volume / 100.0));
                break;
        }
    }

    @Override
    public void updateMute(int zone, boolean mute) {
        switch (zone) {
            case 0:
                updateState(SonyAudioBindingConstants.CHANNEL_MUTE, mute ? OnOffType.ON : OnOffType.OFF);
                break;
            case 1:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE1_MUTE, mute ? OnOffType.ON : OnOffType.OFF);
                break;
            case 2:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE2_MUTE, mute ? OnOffType.ON : OnOffType.OFF);
                break;
            case 3:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE3_MUTE, mute ? OnOffType.ON : OnOffType.OFF);
                break;
            case 4:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE4_MUTE, mute ? OnOffType.ON : OnOffType.OFF);
                break;
        }
    }

    @Override
    public void updatePowerStatus(int zone, boolean power) {
        switch (zone) {
            case 0:
                updateState(SonyAudioBindingConstants.CHANNEL_POWER, power ? OnOffType.ON : OnOffType.OFF);
                break;
            case 1:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE1_POWER, power ? OnOffType.ON : OnOffType.OFF);
                break;
            case 2:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE2_POWER, power ? OnOffType.ON : OnOffType.OFF);
                break;
            case 3:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE3_POWER, power ? OnOffType.ON : OnOffType.OFF);
                break;
            case 4:
                updateState(SonyAudioBindingConstants.CHANNEL_ZONE4_POWER, power ? OnOffType.ON : OnOffType.OFF);
                break;
        }
    }

    private void startAutomaticRefresh(int refresh) {
        if (refresh <= 0) {
            return;
        }

        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                List<Channel> channels = getThing().getChannels();
                for (Channel channel : channels) {
                    handleCommand(channel.getUID(), RefreshType.REFRESH);
                }
            } catch (Exception e) {
                logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
            }
        }, 5, refresh, TimeUnit.SECONDS);
    }
}
