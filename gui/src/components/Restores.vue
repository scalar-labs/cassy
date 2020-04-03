<template>
    <div>
        <h4>Restores</h4>
        <table class="table table-borderless table-striped text-nowrap text-center">
            <thead>
            <tr>
                <th scope="col">Snapshot ID</th>
                <th scope="col">Target IP</th>
                <th scope="col">Created At</th>
                <th scope="col">Updated At</th>
                <th scope="col">Restore Type</th>
                <th scope="col">Status</th>
            </tr>
            </thead>
            <tbody>
            <tr
                    v-for="(r, index) in restores.entries"
                    :key="index"
            >
                <td>{{r.snapshot_id}}</td>
                <td>{{r.target_ip}}</td>
                <td>{{ parseInt(r.created_at) | moment('YYYY/M/d, h:mm a')}}</td>
                <td>{{ parseInt(r.updated_at) | moment('YYYY/M/d h:mm a')}}</td>
                <td>{{r.restore_type}}</td>
                <td>{{r.status}}</td>
            </tr>
            </tbody>
        </table>
        <div class="row justify-content-end">
            <button class="btn btn-secondary" @click="goBack">Back</button>
        </div>
    </div>
</template>

<script>
    export default {
      props: {
        restores: {}
      },
      methods: {
        goBack() {
          this.$router.back()
        }
      }
    }
</script>
