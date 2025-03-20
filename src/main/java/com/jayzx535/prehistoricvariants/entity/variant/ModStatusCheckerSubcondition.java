package com.jayzx535.prehistoricvariants.entity.variant;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.ModList;

public class ModStatusCheckerSubcondition {

	public static ModStatusCheckerSubcondition EMPTY = new ModStatusCheckerSubcondition(false, new LinkedList<>());
	
	public final boolean requireAll;
	protected final List<String> modIds;
	
	public ModStatusCheckerSubcondition(boolean requireAllIn, List<String> modIdsIn) {
		this.requireAll = requireAllIn;
		this.modIds = modIdsIn;
	}
	
	public boolean requiredModsLoaded() {
		// If no mods are specified, return true by default
		if (this.modIds.isEmpty()) return true;
		for (String modId : this.modIds) {
			if (ModList.get().isLoaded(modId)) {
				// If the mod is loaded, validate only if only one entry is required
				if (!this.requireAll) return true;
			} else {
				// If the mod isn't loaded, invalidate if all are required, otherwise continue checking
				if (this.requireAll) return false;
			}
		}
		// Return true if all is required (as we would have exited if one was skipped), otherwise false because no entries were found
		return this.requireAll;
	}
	
	public static ModStatusCheckerSubcondition deserializer(JsonObject jsonIn) {
		JsonArray ids = GsonHelper.getAsJsonArray(jsonIn, "mods", new JsonArray());
		List<String> idList = new LinkedList<>();
		ids.forEach((entry) -> idList.add(entry.getAsString()));
		return new ModStatusCheckerSubcondition(GsonHelper.getAsBoolean(jsonIn, "require_all", false), idList);
	}
	
	public CompoundTag writeToTag() {
		CompoundTag tag = new CompoundTag();
		ListTag idTags = new ListTag();
		this.modIds.forEach((entry) -> {
			CompoundTag idTag = new CompoundTag();
			idTag.putString("Modid", entry);
			idTags.add(idTag);
		});
		if (!idTags.isEmpty()) {
			if (this.requireAll) tag.put("RequireAll", tag);
			tag.put("Mods", idTags);
		}
		return tag;
	}
	
	public static ModStatusCheckerSubcondition readFromTag(CompoundTag tagIn) {
		List<String> idList = new LinkedList<>();
		if (tagIn.contains("Mods")) {
			ListTag mods = tagIn.getList("Mods", 10);
			for(int i = 0; i < mods.size(); ++i) {
				CompoundTag modTag = mods.getCompound(i);
				if (modTag.contains("Modid")) idList.add(modTag.getString("Modid"));
			}
		}
		return new ModStatusCheckerSubcondition(tagIn.contains("RequireAll") ? tagIn.getBoolean("RequireAll") : false, idList);
	}
}
