package com.kernicpanel;

import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class RandomizerExtension extends ControllerExtension {
  Random rand = new Random();
  Faker faker = new Faker();

  private SettableBooleanValue useDate;
  private SettableStringValue dateFormatTemplate;
  private static final String DEFAULT_DATE_FORMAT_TEMPLATE = "yyyy-MM-dd_";

  private ControllerHost host;
  private DocumentState documentState;
  private PopupBrowser popupBrowser;
  private CursorTrack cursorTrack;
  private BrowserResultsItemBank resultsItemBank;

  private SettableStringValue filenameOutput;
  private SettableStringValue nameOutput;

  protected RandomizerExtension(
      final RandomizerExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  private void printer(String s) {
    host.println(s);
    java.lang.System.out.println(s);
  }


  @Override
  public void init() {
    host = getHost();

    documentState = host.getDocumentState();
    popupBrowser = host.createPopupBrowser();
    popupBrowser.exists().markInterested();
    popupBrowser.resultsColumn().entryCount().markInterested();
    cursorTrack = host.createCursorTrack(0, 0);

    resultsItemBank = popupBrowser.resultsColumn().createItemBank(100000);

    useDate =
        host.getPreferences().getBooleanSetting("Prepend date for filename", "Random name", true);
    dateFormatTemplate =
        host.getPreferences().getStringSetting("Format string for date prefix", "Random name", 15, DEFAULT_DATE_FORMAT_TEMPLATE);
    dateFormatTemplate.addValueObserver(value -> {
      try {
        LocalDate.now().format(DateTimeFormatter.ofPattern(value));
      } catch (IllegalArgumentException | UnsupportedTemporalTypeException e) {
         dateFormatTemplate.set(DEFAULT_DATE_FORMAT_TEMPLATE);
         host.showPopupNotification("Invalid date format template.");
      }
    });

    documentState
        .getSignalSetting("Select", "Randomize browser selection", "Select random item")
        .addSignalObserver(selectRandomItem());
    documentState
        .getSignalSetting("Add", "Randomize browser selection", "Add current item")
        .addSignalObserver(popupBrowser::commit);

    filenameOutput = documentState.getStringSetting("Filename", "Random name", 50, "");
    nameOutput = documentState.getStringSetting("Name", "Random name", 50, "");
    documentState.getSignalSetting(" ", "Random name", "Generate").addSignalObserver(randomName());
  }

  private NoArgsCallback selectRandomItem() {
    return () -> {
      if (!popupBrowser.exists().getAsBoolean()) {
        cursorTrack.endOfDeviceChainInsertionPoint().browse();
      }

      host.scheduleTask(
          () -> {
            Integer random = rand.nextInt(popupBrowser.resultsColumn().entryCount().get());
            resultsItemBank.getItemAt(random).isSelected().set(true);
          },
          300);
    };
  }

  private NoArgsCallback randomName() {
    return () -> {
      String[] moods = {faker.mood().emotion(), faker.mood().tone(), faker.mood().feeling()};
      String mood = moods[rand.nextInt(moods.length)];

      String[] names = {
        faker.superhero().power(),
        faker.hacker().ingverb(),
        faker.hacker().noun(),
        faker.hacker().verb(),
      };
      String name = names[rand.nextInt(names.length)];

      String[] generators = {mood, name};
      String generatedString = String.join(" ", Arrays.asList(generators));
      nameOutput.set(generatedString.toLowerCase(Locale.ROOT));

      if (useDate.get()) {
        LocalDate dateObj = LocalDate.now();
        String formattedDate;
        try {
          formattedDate = dateObj.format(DateTimeFormatter.ofPattern(dateFormatTemplate.get()));
        } catch (IllegalArgumentException | UnsupportedTemporalTypeException e) {
          // Should not happen, unless e.g. the config data got corrupted.
          dateFormatTemplate.set(DEFAULT_DATE_FORMAT_TEMPLATE);
          formattedDate = dateObj.format(DateTimeFormatter.ofPattern(dateFormatTemplate.get()));
        }
        generatedString = formattedDate + generatedString;
      }

      filenameOutput.set(generatedString.replace(" ", "_").toLowerCase(Locale.ROOT));
    };
  }

  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
  }

  @Override
  public void flush() {
    // TODO Send any updates you need here.
  }
}
