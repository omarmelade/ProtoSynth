//
// Created by omark on 19/10/2021.
//

#ifndef PROTOSYNTH_AUDIOENGINE_H
#define PROTOSYNTH_AUDIOENGINE_H


#include <aaudio/AAudio.h>
#include "Oscillator.h"

class AudioEngine {
public:
    bool start();
    void stop();
    void restart();
    void setToneOn(bool isToneOn);

    void setFreq(double f);
    void setisSin(bool v){ oscillator_.setSin(v); }

private:
    Oscillator oscillator_;
    AAudioStream *stream_;
};



#endif //PROTOSYNTH_AUDIOENGINE_H
