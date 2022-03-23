package com.amuzil.omegasource.skillapi.listeners;

/**
 * Listeners store activators. Activators have a list of pre-requisites/events.
 * Listeners search for those events and trigger all activators as needed.
 * 1 listener per activator.
 * Activators can have hard-coded pre-requisites or loaded ones from json files.
 * Upon 'skill mode' being activated (whether through a toggle key or something else), all
 * skill listeners are loaded. Listeners that are always active are the toggle listeners/initialise
 * listeners.
 **/
public class DynamicStateListener {
}
