package mekanism.api.datagen.tag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ForgeRegistryTagProvider<TYPE /*extends IForgeRegistryEntry<TYPE>*/> implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator gen;
    private final Map<Tag.Identified<TYPE>, Tag.Builder> tagToBuilder = new Object2ObjectLinkedOpenHashMap<>();
    private final Registry<TYPE> registry;
    protected final String modid;

    protected ForgeRegistryTagProvider(DataGenerator gen, String modid, Registry<TYPE> registry) {
        this.gen = gen;
        this.modid = modid;
        this.registry = registry;
    }

    protected abstract void registerTags();

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void run(@Nonnull DataCache cache) {
        tagToBuilder.clear();
        registerTags();
        if (!tagToBuilder.isEmpty()) {
            Map<Identifier, Tag.Builder> builders = tagToBuilder.entrySet().stream().collect(Collectors.toMap(tag -> tag.getKey().getId(), Entry::getValue));
            builders.forEach((id, tagBuilder) -> {
                Path path = makePath(id);
                try {
                    String json = GSON.toJson(tagBuilder.toJson());
                    String hash = SHA1.hashUnencodedChars(json).toString();
                    if (!Objects.equals(cache.getOldSha1(path), hash) || !Files.exists(path)) {
                        Files.createDirectories(path.getParent());
                        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                            bufferedwriter.write(json);
                        }
                    }
                    cache.updateSha1(path, hash);
                } catch (IOException exception) {
                    LOGGER.error("Couldn't save tags to {}", path, exception);
                }
            });
        }
    }

    protected ForgeRegistryTagBuilder<TYPE> getBuilder(Tag.Identified<TYPE> tag) {
        return new ForgeRegistryTagBuilder<>(tagToBuilder.computeIfAbsent(tag, ignored -> Tag.Builder.create()), modid);
    }

    @Nonnull
    protected abstract Path makePath(Identifier id);
}