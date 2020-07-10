<template>
    <table class="table table-borderless text-left">
        <thead>
        <tr>
            <th scope="col">Cluster ID</th>
            <th scope="col">Target IPs</th>
            <th scope="col">Keyspaces</th>
        </tr>
        </thead>
        <tbody>
        <tr :class="{'no-data': cluster.cluster_id, 'text-center': true, 'font-italic': true}">
            <td colspan="3">Unable to retrieve cluster data</td>
        </tr>
        <tr>
            <td>{{cluster.cluster_id}}</td>
            <td>{{cluster.target_ips}}</td>
            <td>{{cluster.keyspaces}}</td>
        </tr>
        </tbody>
    </table>
</template>

<script>
  export default {
    props: {
      cluster: {},
    },
  };
</script>
