Create another API endpoint for complete the current step of work and move to another step:

    - If the next step isn't the last step of that work refer by it own workType then update the `currentStep` and `currentStepIndex` to the next step
    - Otherwise If the next step is the last step of that work type refer by it own workType the update the `currentStep`, `currentStepIndex` and `status` of work to "FINISHED"