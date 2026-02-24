# Mono to multi region

## Principle

The migration follows these steps:

1. Update the configuration of the original cluster to comply with multi-region requirements.
2. Ensure a backup storage is configured and that the cluster can be successfully backed up.
3. Create a new multi-region cluster and configure it to use the same backup storage.
4. Perform a backup of the original cluster.
5. Restore the backup into the new multi-region cluster.

The original cluster remains unchanged. A new multi-region cluster is prepared, and data from the original cluster is restored from the backup.

This approach allows the new cluster to be prepared in advance, and the procedure can be tested and validated, including measuring the time required for backup and restore.

## Update the configuration


## Backup storage

## Multi-regions

Create a multi-region cluster. Check the procedure [multi-region](../../c8-multi-region/solution/README.md)

## Migrate
Note: this operation can be done multiple time. When the migration is definitive, then do a last time the operation, then stop the mono cluster.

### Perform a backup

### Perform a restore

Dual Region: duplicate the restore on both ElasticSearch
Centralize: execute the restore only one time

Zeebe

