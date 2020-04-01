<template>
    <div class="container">
        <h4 class="mt-3">View Backups</h4>
        <table class="table table-borderless table-striped text-nowrap text-center">
            <thead>
            <tr>
                <th scope="col">#</th>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Target IP</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col">Backup Type</th>
                <th scope="col">Status</th>
            </tr>
            </thead>
            <tbody>
            <tr
                    v-for="(b, index) in backups.entries"
                    :key="index"
            >
                <td scope="row">{{index + 1}}</td>
                <td>{{b.snapshot_id}}</td>
                <td>{{b.target_ip}}</td>
                <td>{{ parseInt(b.created_at) | moment("YYYY/M/d, h:mm a")}}</td>
                <td>{{ parseInt(b.updated_at) | moment("YYYY/M/d h:mm a")}}</td>
                <td>{{b.backup_type}}</td>
                <td>{{b.status}}</td>
                <td><button
                        type="button"
                        class="btn btn-secondary"
                >Restore</button></td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end"><button class="btn btn-primary">Create Backup</button></div>
    </div>
</template>

<script>
  export default {
    props: {
      backups: {
        type: Object,
      },
    },
    methods: {
    }
  };
</script>
