package com.tellem.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.tellem.model.Voiceover;
import com.tellem.model.dto.VoiceDto;
import com.tellem.model.dto.VoiceoverDto;
import com.tellem.service.VoiceoverService;
import jakarta.annotation.PreDestroy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/voiceover")
public class VoiceoverController {

    private final TextToSpeechClient ttsClient;
    private final Map<String, List<String>> randomNames = new HashMap<>();
    private final Map<String, Set<String>> usedNames = new HashMap<>();
    private final Random random = new Random();
    private final VoiceoverService voiceoverService;


    public VoiceoverController(VoiceoverService voiceoverService) throws IOException {
        this.voiceoverService = voiceoverService;
        String credentialsPath = "D:\\College\\Tell-em\\server\\story_voiceover_service.json";
        try {
            FileInputStream credentialsStream = new FileInputStream(credentialsPath);
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);

            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            ttsClient = TextToSpeechClient.create(settings);
        } catch (IOException e) {
            System.err.println("Error loading credentials or creating TTS client: " + e.getMessage());
            throw e;
        }

        // Initialize the used names map
        usedNames.put("MALE", new HashSet<>());
        usedNames.put("FEMALE", new HashSet<>());
    }

    @GetMapping("/")
    public List<VoiceoverDto> getAllVoiceovers() {
        return voiceoverService.getAllVoiceovers();
    }

    @GetMapping("/voices")
    public List<VoiceDto> listVoices() {
        // Initializing random names for the genders
        randomNames.put("MALE", List.of("James", "Benjamin", "Thomas", "Liam", "William", "Elijah", "Carl", "Noah", "Theodore"));
        randomNames.put("FEMALE", List.of("Olivia", "Harper", "Jennifer", "Charlotte", "Blake", "Ava", "Sophia", "Abigail", "Isabella", "Emma", "Mia"));

        ListVoicesRequest request = ListVoicesRequest.newBuilder().setLanguageCode("en-US").build();
        ListVoicesResponse response = ttsClient.listVoices(request);

        List<VoiceDto> voiceInfos = new ArrayList<>();
        for (Voice voice : response.getVoicesList()) {
            String gender = voice.getSsmlGender().toString();
            String displayName = getRandomNameForGender(gender);
            VoiceDto voiceInfo = new VoiceDto(voice.getName(), gender, displayName);
            voiceInfos.add(voiceInfo);
        }
        return voiceInfos;
    }

    private String getRandomNameForGender(String gender) {
        List<String> names = randomNames.getOrDefault(gender, List.of("Voice"));

        // Filter out already used names
        List<String> availableNames = new ArrayList<>();
        for (String name : names) {
            if (!usedNames.get(gender).contains(name)) {
                availableNames.add(name);
            }
        }

        // If all names have been used, reset the set of used names
        if (availableNames.isEmpty()) {
            usedNames.get(gender).clear();
            availableNames = new ArrayList<>(names);
        }

        // Get a random name from the available names
        int randomIndex = random.nextInt(availableNames.size());
        String selectedName = availableNames.get(randomIndex);

        // Mark the selected name as used
        usedNames.get(gender).add(selectedName);

        return selectedName;
    }

    @PostMapping("/tts")
    public ResponseEntity<byte[]> generateTTS(@RequestBody Voiceover request) throws IOException {
        try {
            SynthesisInput input = SynthesisInput.newBuilder().setText(request.getContent()).build();
            VoiceDto voiceDto = request.getVoice();
            VoiceSelectionParams.Builder voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US");

            if (voiceDto.getGender() != null) {
                voice.setSsmlGender(SsmlVoiceGender.valueOf(voiceDto.getGender()));
            }

            if (voiceDto.getName() != null) {
                voice.setName(voiceDto.getName());
            }

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSampleRateHertz(16000)
                    .build();

            SynthesizeSpeechRequest voiceRequest = SynthesizeSpeechRequest.newBuilder()
                    .setInput(input)
                    .setVoice(voice)
                    .setAudioConfig(audioConfig)
                    .build();

            SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(voiceRequest);
            ByteString audioContents = response.getAudioContent();

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("audio/mpeg"))
                    .body(audioContents.toByteArray());

        } catch (Exception e) {
            System.err.println("Error generating voiceover: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> storeVoiceover(@RequestBody VoiceoverDto request) {

        try {
            voiceoverService.saveVoiceover(request);
            return ResponseEntity.ok("Voiceover information stored successfully");
        } catch (Exception e) {
            System.err.println("Error generating voiceover: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PreDestroy
    public void closeTtsClient() {
        if (ttsClient != null) {
            ttsClient.close();
        }
    }
}