//
// Created by omark on 19/10/2021.
//

#ifndef PROTOSYNTH_OSCILLATOR_H
#define PROTOSYNTH_OSCILLATOR_H


#include <atomic>
#include <stdint.h>

class Oscillator {
public:
    void setWaveOn(bool isWaveOn);
    void setSampleRate(int32_t sampleRate);
    void render(float *audioData, int32_t numFrames);

    double freq() const;
    void freq( double f );
    void setSin(bool v){ isSin_ = v; }
    bool isSin(){ return isSin_; }

private:
    std::atomic<bool> isWaveOn_{false};
    double phase_ = 0.0;
    double phaseIncrement_ = 0.0;
    double frequency = 440.0;
    bool isSin_ = true;
};



#endif //PROTOSYNTH_OSCILLATOR_H
