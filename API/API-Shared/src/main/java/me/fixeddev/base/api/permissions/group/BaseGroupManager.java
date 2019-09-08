package me.fixeddev.base.api.permissions.group;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.ObjectRepository;

import java.util.Objects;

public class BaseGroupManager implements GroupManager {

    @Inject
    private ObjectRepository<Group> groupObjectRepository;

    @Override
    public ListenableFuture<Group> getGroupByName(String name) {
        return groupObjectRepository.findOne(name);
    }

    @Override
    public ListenableFuture<Group> createGroup(String name) {
        return createGroup(name, 1);
    }

    @Override
    public ListenableFuture<Group> createGroup(String name, int weight) {
        return Futures.transform(existsGroupWithName(name), (Function<Boolean, Group>) exists -> {
            exists = exists != null ? exists : false;

            if (exists) {
                return null;
            }

            Group group = new Group(name, weight);
            groupObjectRepository.save(group);

            return group;
        });
    }


    @Override
    public ListenableFuture<Boolean> existsGroupWithName(String name) {
        return Futures.transform(groupObjectRepository.findOne(name), Objects::nonNull);
    }

    @Override
    public ListenableFuture<Group> deleteGroupByName(String name) {
        return Futures.transform(getGroupByName(name), (Function<Group, Group>) group -> {
            if (group == null) {
                return null;
            }

            groupObjectRepository.delete(group);

            return group;
        });
    }
}
