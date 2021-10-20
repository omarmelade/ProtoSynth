//
// Created by omark on 19/10/2021.
//

#include "Oscillator.h"
#include <math.h>

#define TWO_PI (3.14159 * 2)
#define AMPLITUDE 0.3


double Oscillator::freq() const { return frequency; }
void Oscillator::freq( double f ) {frequency = f;};


void Oscillator::setSampleRate(int32_t sampleRate) {
    phaseIncrement_ = (TWO_PI * freq()) / (double) sampleRate;
}

void Oscillator::setWaveOn(bool isWaveOn) {
    isWaveOn_.store(isWaveOn);
}

void Oscillator::render(float *audioData, int32_t numFrames) {

    if (!isWaveOn_.load()) phase_ = 0;

    for (int i = 0; i < numFrames; i++) {

        if (isWaveOn_.load()) {

            if(isSin()){
                // Calculates the next sample value for the sine wave.
                audioData[i] = (float) (sin(phase_) * AMPLITUDE);
            }else{
                // Calculates the next sample value for the sine wave.
                audioData[i] = (float) (sqrt(phase_) * AMPLITUDE);
            }


            // Increments the phase, handling wrap around.
            phase_ += phaseIncrement_;
            if (phase_ > TWO_PI) phase_ -= TWO_PI;

        } else {
            // Outputs silence by setting sample value to zero.
            audioData[i] = 0;
        }
    }
}
