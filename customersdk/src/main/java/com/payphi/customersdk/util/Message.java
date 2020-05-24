package com.payphi.customersdk.util;

/**
 * Created by swapnil.g on 1/22/2018.
 */
public enum Message {
    VERIFYERR10(10), VERIFYERR11(11),VERIFYERR12(12),VERIFYERR13(13),VERIFYERR14(14),VERIFYERR15(15),VERIFYERR16(16),VERIFYERR17(17),VERIFYERR18(18);

    private Message(int message){
        this.message = message;
    }

    private int message;

    public int getMessage(){
        return this.message;
    }


}
