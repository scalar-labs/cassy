<template>
    <div id="errorNotificationToast" class="toast" data-animation="true" data-autohide="false" style="position: absolute; top: 1pc; right: 50pc; margin-right: -300px">
        <div class="toast-header">
            <strong class="mr-auto text-danger">Alert</strong>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast">
                <span>&times;</span>
            </button>
        </div>
        <div class="toast-body">
            {{error_message}}
        </div>
    </div>
</template>

<script>
    export default {
      props: {
        error_message: String,
      }
    }
</script>
