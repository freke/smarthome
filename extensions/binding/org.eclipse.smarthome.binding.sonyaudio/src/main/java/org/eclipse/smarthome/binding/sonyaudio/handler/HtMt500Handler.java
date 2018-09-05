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
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.library.types.StringType;

/**
 * The {@link HtMt500Handler} is responsible for handling commands for HT-ST500, which are
 * sent to one of the channels.
 *
 * @author David Åberg - Initial contribution
 */
public class HtMt500Handler extends SonyAudioHandler {

    public HtMt500Handler(Thing thing) {
        super(thing);
    }

    @Override
    public String setInputCommand(Command command){
        switch(command.toString().toLowerCase()){
            case "btaudio": return "extInput:btaudio";
            case "tv": return "extInput:tv";
            case "analog": return "extInput:line";
            case "usb": return "storage:usb1";
            case "network": return "dlna:music";
        }
        return command.toString();
    }

    @Override
    public StringType inputSource(String input){
        String in = input.toLowerCase();
        if(in.contains("extinput:btaudio".toLowerCase())){
            return new StringType("btaudio");
        }
        if(in.contains("extinput:tv".toLowerCase())){
            return new StringType("tv");
        }
        if(in.contains("extinput:line".toLowerCase())){
            return new StringType("analog");
        }
        if(in.contains("storage:usb1".toLowerCase())){
            return new StringType("usb");
        }
        if(in.contains("dlna:music".toLowerCase())){
            return new StringType("network");
        }
        return new StringType(input);
    }
}
