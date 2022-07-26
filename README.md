


# Randomizer

Bitwig randomizer

@polarity did a review for the version 1.0
https://www.youtube.com/watch?v=KXkJyn4Qsho

## WARNING

This WILL lead to unexpected results, be sure to lower your volume before using.

## Motivation

I enjoy surprises, and I think that creativity comes from constraints.

So I wanted to make a script that would randomly select a Bitwig browser item.
This allows me two main things:
- Select unexpected items, which open up possibilities for me to explore.
- Force me to use a forgotten device, plugin… etc. to try to master it.

I added a name genarator, wich tries to be inspiring.
It can be used to name devices, tracks, presets, etc., but also to name projects.

## Activation

Copy the released extension to your Bitwig extensions folder.

Activate it in the settings.


https://user-images.githubusercontent.com/720491/178076494-40867f31-c326-4319-8b38-6d4cedb6aeaa.mp4


## Usage

### Parameters randomization

Devices parameters can be randomized.
A ratio can be applied to limit the range of the new value.
We can randomize the current parameters page or all of them.


https://user-images.githubusercontent.com/720491/180964015-50166694-b680-47f7-b954-8091a33ea6e3.mp4


### Browser random selection

This extension allows users to select a random item from the browser.
It could be any browser related item type, like device, plugin, wave file, preset, modulator…

- `Select random item` opens a browser if none is already opened and selects a random item from the current opened browser.

- `Add current item` adds the current selected item.
  This is the same as `OK` in the browser.
  For me, it is convenient as it is close to the random button.


https://user-images.githubusercontent.com/720491/178076578-f902a6bb-7716-4c7d-8d67-cb6ed1db0af2.mp4


### Random name generator

An option is available to prepend the generated filename with the current date.

https://user-images.githubusercontent.com/720491/178076594-a29e99ac-e81d-426d-8bca-ff4e8bfc7013.mp4


Another option lets you customize the date format. The default template is
`yyyy-MM-DD_` which would expand to e.g. `2022-07-31_`. 
All the possible patterns are docuemnted 
[here](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns).
