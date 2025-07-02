package com.tellem.benchmark;

import com.tellem.model.dto.FrameDto;
import com.tellem.model.dto.MultipleBenchmarkDto;
import com.tellem.model.dto.StoryDto;
import com.tellem.service.AwsS3Service;
import com.tellem.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InsertBranchingStories {

    private final StoryService storyService;
    public static final List<String> RANDOM_FRAME_CONTENTS = List.of(
            "You wake up in an unfamiliar place.",
            "A strange sound echoes in the distance.",
            "You find a torn map on the ground.",
            "A soft breeze whispers your name.",
            "A glowing symbol appears on your hand.",
            "You notice footprints leading into the woods.",
            "A hidden path reveals itself behind the trees.",
            "The sun flickers as if uncertain.",
            "You hear laughter, but no one is around.",
            "You trip over something... it's a journal.",
            "A raven watches you silently.",
            "The forest suddenly goes quiet.",
            "You see a flickering campfire ahead.",
            "A voice in your head says 'Turn back.'",
            "You reach a fork in the road.",
            "A child runs past, giggling, then vanishes.",
            "A bridge stands over a black river.",
            "You open a rusted gate and step inside.",
            "A mirror reflects someone else.",
            "The sky turns from blue to violet.",
            "A small robot follows you.",
            "You find a locked chest with strange carvings.",
            "The wind forms words in the sand.",
            "You step into a library with no roof.",
            "Your reflection blinks at you.",
            "The stars begin to move strangely.",
            "You find a letter addressed to you.",
            "A spiral staircase leads underground.",
            "A neon sign flickers: 'EXIT'.",
            "You find a floating orb humming gently.",
            "Someone whispers from the shadows.",
            "You stumble upon an ancient ruin.",
            "The floor glows under your feet.",
            "A metallic voice says 'Welcome, traveler.'",
            "You walk through a field of glowing flowers.",
            "You find a photo of yourself, aged 20 years.",
            "A wind chime rings with no wind.",
            "You open a door into another season.",
            "A compass spins wildly in your hand.",
            "Your shadow begins to move on its own.",
            "A tree begins to speak.",
            "You meet someone who looks exactly like you.",
            "You find a sword buried in a stone.",
            "A hologram appears mid-air.",
            "You hear music, but can’t find the source.",
            "You follow glowing footprints in the dark.",
            "You touch a rune and feel weightless.",
            "A tower appears on the horizon.",
            "You fall into a memory not your own.",
            "A clock ticks backwards.",
            "You receive a message from the future.",
            "You enter a room with no doors.",
            "The stars rearrange themselves into symbols.",
            "You find a glowing feather.",
            "Your hands begin to glow faintly.",
            "You meet a creature made of smoke.",
            "You feel like you're being watched.",
            "You enter a city that doesn’t exist on any map.",
            "Your voice echoes before you speak.",
            "You cross a bridge made of light.",
            "A floating book opens by itself.",
            "You open your eyes and the world is grayscale.",
            "You find a staircase going up forever.",
            "A cat speaks to you.",
            "You hold a key that feels familiar.",
            "The moon splits into two.",
            "You find a note: 'Don’t trust him.'",
            "A door stands in the middle of a field.",
            "You wake up in a different body.",
            "A butterfly lands on your finger and speaks.",
            "You find a crystal with your name in it.",
            "A blinding light surrounds you.",
            "You see a train station with no rails.",
            "Time seems to pause for everyone but you.",
            "You’re in a desert, but it’s snowing.",
            "You walk through a wall like it’s smoke.",
            "A girl hands you a red balloon.",
            "You remember something that never happened.",
            "The ground below you becomes glass.",
            "You find yourself underwater but can breathe.",
            "You hear your heartbeat... from a tree.",
            "Your reflection smiles before you do.",
            "You follow a bird made of fire.",
            "A street sign reads: 'Memory Lane'.",
            "You sit in a cinema alone. The screen shows your dreams.",
            "You step into a room and gravity shifts.",
            "A tree grows instantly in front of you.",
            "You touch water and see a future flash.",
            "You speak and someone else answers in your voice.",
            "You open your hand. It holds a tiny galaxy.",
            "A child draws something, and it becomes real.",
            "A doorbell rings. No one is there.",
            "You step through fog and into a forgotten world.",
            "You find a ticket: 'Admit One – Destiny'.",
            "A stranger gives you your own diary.",
            "You follow a melody only you can hear.",
            "A crack opens in the sky.",
            "You hold a coin with two identical sides.",
            "You find a room full of clocks, all frozen.",
            "The ocean pulls you gently inland.",
            "A butterfly leads the way.",
            "You light a match and time rewinds one minute.",
            "A planet rises instead of the moon."
    );

    private int storyDepth = 2;
    private int branchFactor = 2;
    @Autowired
    private AwsS3Service s3Service;

    public InsertBranchingStories(StoryService storyService) {
        this.storyService = storyService;
    }

    public MultipleBenchmarkDto generate(int numberOfStories, int numberOfNodes) {
        long startTime = System.currentTimeMillis();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long nodeStart = 0;
        int totalNodes = 0;
        long nodeEnd = 0;
        long memoryAfterNode = 0;

        for (int i = 0; i < numberOfStories; i++) {
            StoryDto storyDto = generateStoryDto(i, numberOfNodes, storyDepth, branchFactor);
            nodeStart = System.currentTimeMillis();
            totalNodes = numberOfStories * numberOfNodes;

            storyService.createGraphFromInput(storyDto);
            nodeEnd = System.currentTimeMillis();
            memoryAfterNode = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        }

        long totalTimeForNodes = nodeEnd - nodeStart;
        double avgTimePerNode = (double) totalTimeForNodes / totalNodes;

        long endTime = System.currentTimeMillis();
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalTime = endTime - startTime;

        return new MultipleBenchmarkDto(
                numberOfStories,
                totalTime,
                (double) totalTime / numberOfStories,
                memoryAfter - memoryBefore,
                numberOfNodes,
                totalTimeForNodes, avgTimePerNode, memoryAfterNode
        );
    }

    private StoryDto generateStoryDto(int index, int numberOfNodes, int storyDepth, int branchFactor) {
        StoryDto storyDto = new StoryDto();
        List<String> FEATURE_IMAGES = s3Service.getAllImagesFromBucket();
        String randomImage = FEATURE_IMAGES.get(new Random().nextInt(FEATURE_IMAGES.size()));

        String[] prefixes = {"Tales of", "Echoes of", "The Legend of", "Chronicles of", "Whispers from", "Journey to"};
        String[] adjectives = {"Silent", "Forgotten", "Burning", "Frozen", "Lost", "Hidden", "Eternal", "Shattered"};
        String[] nouns = {"Flame", "Valley", "Kingdom", "Path", "Whisper", "Storm", "Shadow", "Dream", "Ruin"};
        String[] contexts = {"the North", "Eternity", "Darkness", "Time", "Ashes", "the Sea", "Infinity", "the Ancients"};

        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            String title = prefixes[r.nextInt(prefixes.length)] + " " +
                    adjectives[r.nextInt(adjectives.length)] + " " +
                    nouns[r.nextInt(nouns.length)] + " of " +
                    contexts[r.nextInt(contexts.length)];
            storyDto.setTitle(title);
        }
        storyDto.setDescription("Description " + index);
        storyDto.setFeatureImage(randomImage);

        List<FrameDto> allFrames = new ArrayList<>();
        Map<UUID, FrameDto> frameMap = new HashMap<>();
        Queue<FrameDto> queue = new LinkedList<>();

        FrameDto root = createFrame(0);
        allFrames.add(root);
        frameMap.put(root.getFrameId(), root);
        queue.add(root);
        int created = 1;

        while (created < numberOfNodes && !queue.isEmpty()) {
            FrameDto current = queue.poll();
            List<UUID> nextIds = new ArrayList<>();

            int branches = Math.min(branchFactor, numberOfNodes - created);
            for (int b = 0; b < branches && created < numberOfNodes; b++) {
                FrameDto child = createFrame(created++);
                frameMap.put(child.getFrameId(), child);
                allFrames.add(child);
                queue.add(child);
                nextIds.add(child.getFrameId());

                FrameDto prev = child;
                for (int d = 0; d < storyDepth && created < numberOfNodes; d++) {
                    FrameDto deep = createFrame(created++);
                    frameMap.put(deep.getFrameId(), deep);
                    allFrames.add(deep);
                    prev.setNextFrameIds(List.of(deep.getFrameId()));
                    queue.add(deep);
                    prev = deep;
                }
            }

            current.setNextFrameIds(nextIds);
        }

        storyDto.setFrames(allFrames);
        storyDto.setFirstFrameId(root.getFrameId());
        return storyDto;
    }


    private FrameDto createFrame(int i) {
        String randomContent = RANDOM_FRAME_CONTENTS.get(new Random().nextInt(RANDOM_FRAME_CONTENTS.size()));
        List<String> FEATURE_IMAGES = s3Service.getAllImagesFromBucket();
        String randomImage = FEATURE_IMAGES.get(new Random().nextInt(FEATURE_IMAGES.size()));
        FrameDto frame = new FrameDto();
        frame.setFrameId(UUID.randomUUID());
        frame.setContent(randomContent);
        frame.setImage(randomImage);
        frame.setNextFrameIds(new ArrayList<>());
        return frame;
    }

    public void setConfig(int storyDepth, int branchFactor) {
        this.storyDepth = storyDepth;
        this.branchFactor = branchFactor;
    }
}
